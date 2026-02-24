import SwiftUI
import UIKit
import composeKit

struct ContentView: View {
    private static let tabBarContentInset: CGFloat = 72

    @State private var selectedTab: Int = 0
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

    private static let checkoutRequestId: String = {
        guard let value = Bundle.main.infoDictionary?["ConektaCheckoutRequestId"] as? String, !value.isEmpty else {
            return "0f3e251c-90b7-4846-9ecb-e48b447f25e4"
        }
        return value
    }()

    private static let checkoutJwtToken: String = {
        guard let value = Bundle.main.infoDictionary?["ConektaJwtToken"] as? String, !value.isEmpty else {
            return "jwt_mock_123"
        }
        return value
    }()

    private static let checkoutBaseUrl = "https://services.stg.conekta.io/checkout-bff/v1/"

    var body: some View {
        TabView(selection: $selectedTab) {
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
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: ContentView.tabBarContentInset)
            }
            .tabItem {
                Label("Tokenizer", systemImage: "creditcard")
            }
            .tag(0)

            ConektaCheckoutView(
                config: CheckoutConfig(
                    checkoutRequestId: ContentView.checkoutRequestId,
                    publicKey: ContentView.conektaPublicKey,
                    jwtToken: ContentView.checkoutJwtToken,
                    merchantName: "My Store",
                    baseUrl: ContentView.checkoutBaseUrl
                ),
                onPaymentMethodSelected: { method in
                    print("Payment method selected: \(method)")
                },
                onError: { error in
                    print("Checkout error: \(error)")
                },
                onOrderCreated: { result in
                    print("Order created: orderId=\(result.orderId)")
                }
            )
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: ContentView.tabBarContentInset)
            }
            .tabItem {
                Label("Checkout", systemImage: "cart")
            }
            .tag(1)
        }
        .alert(alertTitle, isPresented: $showingAlert) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(alertMessage)
        }
    }
}

private struct ConektaCheckoutView: UIViewControllerRepresentable {
    let config: CheckoutConfig
    let onPaymentMethodSelected: (String) -> Void
    let onError: (CheckoutError) -> Void
    var onOrderCreated: ((CheckoutOrderResult) -> Void)? = nil

    func makeUIViewController(context: Context) -> UIViewController {
        ConektaCheckoutViewControllerKt.ConektaCheckoutViewController(
            config: config,
            onPaymentMethodSelected: onPaymentMethodSelected,
            onError: onError,
            onOrderCreated: onOrderCreated
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
