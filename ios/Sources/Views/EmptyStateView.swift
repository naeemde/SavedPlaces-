import SwiftUI

struct EmptyStateView: View {
    var body: some View {
        ContentUnavailableView(
            "لا توجد أماكن محفوظة بعد",
            systemImage: "mappin.slash",
            description: Text("اضغط على زر \"حفظ موقعي الحالي\" بالأسفل لإضافة أول مكان")
        )
    }
}

#Preview {
    EmptyStateView()
}
