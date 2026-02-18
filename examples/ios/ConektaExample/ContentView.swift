import SwiftUI
import composeKit

struct ContentView: View {
    @State private var showingAlert = false
    @State private var alertTitle = ""
    @State private var alertMessage = ""

    // Replace with your Conekta public key (see README)
    private static let conektaPublicKey = "YOUR_PUBLIC_KEY_HERE"

    var body: some View {
        ConektaTokenizerView(
            config: SharedTokenizerConfig(
                publicKey: ContentView.conektaPublicKey,
                merchantName: "My Store",
                collectCardholderName: true,
                baseUrl: "https://api.conekta.io/",
                rsaPublicKey: ""
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
                alertMessage = "Payment could not be processed. Please try again."
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
