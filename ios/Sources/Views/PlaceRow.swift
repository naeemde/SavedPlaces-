import SwiftUI

struct PlaceRow: View {
    let place: Place

    @Environment(\.openURL) private var openURL

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Image(systemName: "mappin.circle.fill")
                    .foregroundStyle(.tint)
                Text(place.name)
                    .font(.headline)
            }

            Text("\(place.latitude, specifier: "%.5f"), \(place.longitude, specifier: "%.5f")")
                .font(.caption)
                .foregroundStyle(.secondary)

            Text(place.createdAt, format: .dateTime.day().month().year().hour().minute())
                .font(.caption)
                .foregroundStyle(.secondary)

            HStack(spacing: 12) {
                Button {
                    openURL(MapsLauncher.navigationURL(for: place))
                } label: {
                    Label("التنقل", systemImage: "location.north.line.fill")
                }
                .buttonStyle(.bordered)

                ShareLink(item: MapsLauncher.shareURL(for: place), subject: Text(place.name)) {
                    Label("مشاركة", systemImage: "square.and.arrow.up")
                }
                .buttonStyle(.bordered)
            }
            .font(.subheadline)
            .padding(.top, 4)
        }
        .padding(.vertical, 6)
    }
}

#Preview {
    List {
        PlaceRow(place: Place(name: "بيت أبي", latitude: 32.0685535, longitude: 44.3292355))
    }
}
