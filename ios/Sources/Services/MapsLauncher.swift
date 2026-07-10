import Foundation

/// أدوات بناء روابط خرائط آبل (مجانية بالكامل، بدون أي مفتاح API) للتنقل والمشاركة.
enum MapsLauncher {

    /// رابط يفتح تطبيق خرائط آبل بمسار ملاحة من الموقع الحالي إلى هذا المكان.
    static func navigationURL(for place: Place) -> URL {
        var components = URLComponents(string: "https://maps.apple.com/")!
        components.queryItems = [
            URLQueryItem(name: "daddr", value: "\(place.latitude),\(place.longitude)"),
            URLQueryItem(name: "dirflg", value: "d")
        ]
        return components.url!
    }

    /// رابط قابل للمشاركة مع أي تطبيق آخر (واتساب، رسائل، بريد...).
    static func shareURL(for place: Place) -> URL {
        var components = URLComponents(string: "https://maps.apple.com/")!
        components.queryItems = [
            URLQueryItem(name: "ll", value: "\(place.latitude),\(place.longitude)"),
            URLQueryItem(name: "q", value: place.name)
        ]
        return components.url!
    }
}
