import CoreLocation
import Observation

/// غلاف حول CLLocationManager (مجاني بالكامل، جزء من نظام iOS) يوفر
/// طلب الإذن وجلب الموقع الحالي بشكل متزامن (async/await) بدلاً من نمط delegate التقليدي.
@Observable
final class LocationManager: NSObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    private(set) var authorizationStatus: CLAuthorizationStatus = .notDetermined

    private var locationContinuation: CheckedContinuation<CLLocationCoordinate2D, Error>?
    private var authContinuation: CheckedContinuation<CLAuthorizationStatus, Never>?

    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        authorizationStatus = manager.authorizationStatus
    }

    /// يطلب إذن الموقع فقط إذا لم يُطلب من قبل، وينتظر قرار المستخدم فعلياً قبل المتابعة.
    func requestPermissionIfNeeded() async -> CLAuthorizationStatus {
        if authorizationStatus != .notDetermined {
            return authorizationStatus
        }
        return await withCheckedContinuation { continuation in
            authContinuation = continuation
            manager.requestWhenInUseAuthorization()
        }
    }

    /// يجلب الموقع الحالي مرة واحدة فقط (وليس تحديثات مستمرة).
    func getCurrentLocation() async throws -> CLLocationCoordinate2D {
        try await withCheckedThrowingContinuation { continuation in
            locationContinuation = continuation
            manager.requestLocation()
        }
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatus = manager.authorizationStatus
        if let authContinuation = authContinuation {
            self.authContinuation = nil
            authContinuation.resume(returning: manager.authorizationStatus)
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        locationContinuation?.resume(returning: location.coordinate)
        locationContinuation = nil
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        locationContinuation?.resume(throwing: error)
        locationContinuation = nil
    }
}
