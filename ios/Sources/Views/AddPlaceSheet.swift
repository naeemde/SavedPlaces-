import SwiftUI
import CoreLocation

/// نافذة تظهر بعد تحديد الموقع الحالي بنجاح، لإعطائه اسماً قبل حفظه.
struct AddPlaceSheet: View {
    let coordinate: CLLocationCoordinate2D
    let onSave: (String) -> Void

    @State private var name: String = ""
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            Form {
                Section("اسم هذا الموقع") {
                    TextField("مثال: بيت أبي، العمل", text: $name)
                }
                Section {
                    Text("\(coordinate.latitude, specifier: "%.5f"), \(coordinate.longitude, specifier: "%.5f")")
                        .foregroundStyle(.secondary)
                }
            }
            .navigationTitle("موقع جديد")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("إلغاء") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("حفظ") { onSave(name) }
                }
            }
        }
    }
}

#Preview {
    AddPlaceSheet(coordinate: CLLocationCoordinate2D(latitude: 32.0685535, longitude: 44.3292355)) { _ in }
}
