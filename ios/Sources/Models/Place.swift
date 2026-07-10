import Foundation
import SwiftData

/// يمثل مكاناً واحداً محفوظاً: اسمه، وإحداثياته، وتاريخ حفظه.
@Model
final class Place {
    var name: String
    var latitude: Double
    var longitude: Double
    var createdAt: Date

    init(name: String, latitude: Double, longitude: Double, createdAt: Date = Date()) {
        self.name = name
        self.latitude = latitude
        self.longitude = longitude
        self.createdAt = createdAt
    }
}
