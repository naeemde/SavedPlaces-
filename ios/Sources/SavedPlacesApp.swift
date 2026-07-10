import SwiftUI
import SwiftData

@main
struct SavedPlacesApp: App {
    var body: some Scene {
        WindowGroup {
            PlacesListView()
        }
        .modelContainer(for: Place.self)
    }
}
