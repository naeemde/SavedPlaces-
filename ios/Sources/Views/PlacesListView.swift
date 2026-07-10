import SwiftUI
import SwiftData
import CoreLocation

struct PlacesListView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \Place.createdAt, order: .reverse) private var places: [Place]

    @State private var locationManager = LocationManager()
    @State private var isFetchingLocation = false
    @State private var pendingCoordinate: CLLocationCoordinate2D?
    @State private var showNameSheet = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottom) {
                Group {
                    if places.isEmpty {
                        EmptyStateView()
                    } else {
                        List {
                            ForEach(places) { place in
                                PlaceRow(place: place)
                            }
                            .onDelete(perform: deletePlaces)
                        }
                    }
                }

                saveButton
                    .padding(.bottom, 16)
            }
            .navigationTitle("أماكني المحفوظة")
            .sheet(isPresented: $showNameSheet) {
                if let pendingCoordinate = pendingCoordinate {
                    AddPlaceSheet(coordinate: pendingCoordinate) { name in
                        savePlace(name: name, coordinate: pendingCoordinate)
                        showNameSheet = false
                    }
                }
            }
            .alert("خطأ", isPresented: errorBinding) {
                Button("حسناً") { errorMessage = nil }
            } message: {
                Text(errorMessage ?? "")
            }
        }
    }

    private var saveButton: some View {
        Button {
            saveCurrentLocation()
        } label: {
            HStack {
                if isFetchingLocation {
                    ProgressView()
                        .tint(.white)
                } else {
                    Image(systemName: "plus.circle.fill")
                }
                Text(isFetchingLocation ? "جارٍ تحديد الموقع..." : "حفظ موقعي الحالي")
            }
            .font(.headline)
            .foregroundStyle(.white)
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .background(Capsule().fill(Color.accentColor))
        }
        .disabled(isFetchingLocation)
    }

    private var errorBinding: Binding<Bool> {
        Binding(get: { errorMessage != nil }, set: { if !$0 { errorMessage = nil } })
    }

    private func saveCurrentLocation() {
        Task {
            isFetchingLocation = true
            defer { isFetchingLocation = false }

            let status = await locationManager.requestPermissionIfNeeded()
            guard status == .authorizedWhenInUse || status == .authorizedAlways else {
                errorMessage = "يجب منح إذن الموقع حتى يستطيع التطبيق حفظ مكانك الحالي"
                return
            }

            do {
                let coordinate = try await locationManager.getCurrentLocation()
                pendingCoordinate = coordinate
                showNameSheet = true
            } catch {
                errorMessage = "تعذّر تحديد الموقع الحالي، تأكد من تفعيل خدمة الموقع على جهازك"
            }
        }
    }

    private func savePlace(name: String, coordinate: CLLocationCoordinate2D) {
        let trimmedName = name.trimmingCharacters(in: .whitespacesAndNewlines)
        let place = Place(
            name: trimmedName.isEmpty ? "موقع محفوظ" : trimmedName,
            latitude: coordinate.latitude,
            longitude: coordinate.longitude
        )
        modelContext.insert(place)
    }

    private func deletePlaces(at offsets: IndexSet) {
        for index in offsets {
            modelContext.delete(places[index])
        }
    }
}

#Preview {
    PlacesListView()
        .modelContainer(for: Place.self, inMemory: true)
}
