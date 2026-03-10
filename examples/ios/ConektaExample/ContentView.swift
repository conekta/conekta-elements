import SwiftUI
import composeKit

struct ContentView: View {
    @State private var showingAlert = false
    @State private var alertTitle = ""
    @State private var alertMessage = ""

    // Set CONEKTA_PUBLIC_KEY in Local.xcconfig (see README)
    private static let conektaPublicKey: String = {
        guard let key = Bundle.main.infoDictionary?["ConektaPublicKey"] as? String, !key.isEmpty else {
            fatalError("CONEKTA_PUBLIC_KEY not set in Local.xcconfig")
        }
        return key
    }()

    var body: some View {
        ConektaTokenizerView(
            config: TokenizerConfig(
                publicKey: ContentView.conektaPublicKey,
                merchantName: "My Store",
                collectCardholderName: true
            ),
            onSuccess: { tokenResult in
                alertTitle = "Token Created"
                alertMessage = """
                Token: \(tokenResult.token)
                Last 4: \(tokenResult.lastFour)
                """
                showingAlert = true
            },
            onError: { error in
                alertTitle = "Error"
                if let apiError = error as? TokenizerError.TokenizerApiError {
                    alertMessage = "\(apiError.code): \(apiError.message)"
                } else if let networkError = error as? TokenizerError.TokenizerNetworkError {
                    alertMessage = networkError.message
                } else {
                    alertMessage = "Payment could not be processed. Please try again."
                }
                showingAlert = true
            }
        )
        .ignoresSafeArea(.container, edges: .bottom)
        .alert(alertTitle, isPresented: $showingAlert) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(alertMessage)
        }
    }
}
