import SwiftUI
import UIKit
import composeKit

struct ContentView: View {
    private static let tabBarContentInset: CGFloat = 72

    @State private var selectedTab: Int = 0
    @State private var showingAlert = false
    @State private var alertTitle = ""
    @State private var alertMessage = ""
    @State private var checkoutRequestId: String? = nil

    // Set CONEKTA_PUBLIC_KEY in Local.xcconfig (see README)
    private static let conektaPublicKey: String = {
        guard let key = Bundle.main.infoDictionary?["ConektaPublicKey"] as? String, !key.isEmpty else {
            fatalError("CONEKTA_PUBLIC_KEY not set in Local.xcconfig")
        }
        return key
    }()

    private static let checkoutJwtToken: String = {
        guard let value = Bundle.main.infoDictionary?["ConektaJwtToken"] as? String, !value.isEmpty else {
            return "jwt_mock_123"
        }
        return value
    }()

    private static let tokenizerRsaPublicKey: String = {
        guard
            let value = Bundle.main.infoDictionary?["ConektaTokenizerRsaPublicKey"] as? String,
            !value.isEmpty
        else {
            fatalError("CONEKTA_TOKENIZER_RSA_PUBLIC_KEY not set in Local.xcconfig")
        }
        return value
    }()

    private static let apiBaseUrl: String = {
        guard let value = Bundle.main.infoDictionary?["ConektaApiBaseUrl"] as? String, !value.isEmpty else {
            fatalError("CONEKTA_API_BASE_URL not set in Local.xcconfig")
        }
        return value
    }()

    private static let checkoutBaseUrl: String = {
        guard let value = Bundle.main.infoDictionary?["ConektaCheckoutBaseUrl"] as? String, !value.isEmpty else {
            fatalError("CONEKTA_CHECKOUT_BASE_URL not set in Local.xcconfig")
        }
        return value
    }()

    private static let ordersUrl: String = { "\(apiBaseUrl)/orders" }()

    var body: some View {
        TabView(selection: $selectedTab) {
            ConektaTokenizerView(
                config: TokenizerConfig(
                    publicKey: ContentView.conektaPublicKey,
                    merchantName: "My Store",
                    collectCardholderName: true,
                    baseUrl: ContentView.apiBaseUrl,
                    rsaPublicKey: ContentView.tokenizerRsaPublicKey
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

            Group {
                if let checkoutRequestId = checkoutRequestId {
                    ConektaCheckoutView(
                        config: CheckoutConfig(
                            checkoutRequestId: checkoutRequestId,
                            publicKey: ContentView.conektaPublicKey,
                            jwtToken: ContentView.checkoutJwtToken,
                            merchantName: "My Store",
                            baseUrl: ContentView.checkoutBaseUrl,
                            tokenizerBaseUrl: ContentView.apiBaseUrl,
                            tokenizerRsaPublicKey: ContentView.tokenizerRsaPublicKey
                        ),
                        onPaymentMethodSelected: { method in
                            print("Payment method selected: \(method)")
                        },
                        onError: { error in
                            if let apiError = error as? CheckoutError.CheckoutApiError {
                                print("Checkout error api: code=\(apiError.code), message=\(apiError.message)")
                            } else if let networkError = error as? CheckoutError.CheckoutNetworkError {
                                print("Checkout error network: \(networkError.message)")
                            } else if let validationError = error as? CheckoutError.CheckoutValidationError {
                                print("Checkout error validation: \(validationError.message)")
                            } else {
                                print("Checkout error: \(error)")
                            }
                        },
                        onOrderCreated: { result in
                            print("Order created: orderId=\(result.orderId)")
                            print(
                                """
                                Order success payload:
                                status=\(result.status)
                                urlRedirect=\(result.urlRedirect)
                                nextActionType=\(result.nextAction?.type ?? "")
                                nextActionUrl=\(result.nextAction?.redirectToUrl?.url ?? "")
                                nextActionReturnUrl=\(result.nextAction?.redirectToUrl?.returnUrl ?? "")
                                """
                            )
                        }
                    )
                } else {
                    VStack {
                        ProgressView()
                        Text("Creating order...")
                            .padding(.top, 8)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: ContentView.tabBarContentInset)
            }
            .tabItem {
                Label("Checkout", systemImage: "cart")
            }
            .tag(1)
        }
        .task {
            checkoutRequestId = await ContentView.fetchCheckoutRequestId()
        }
        .alert(alertTitle, isPresented: $showingAlert) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(alertMessage)
        }
    }
    private static func fetchCheckoutRequestId() async -> String? {
        guard let url = URL(string: ordersUrl) else { return nil }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(conektaPublicKey)", forHTTPHeaderField: "Authorization")
        request.setValue(
            Locale.current.languageCode ?? "es",
            forHTTPHeaderField: "Accept-Language"
        )
        request.setValue(NetworkHeadersKt.HEADER_ACCEPT_CONEKTA_VERSION, forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let quantity = Int.random(in: 1...10)
        let unitPrice = Int.random(in: 2000...5000)
        let customerName = ["Ana García", "Carlos López", "María Martínez",
                            "Juan Rodríguez", "Laura Sánchez", "Pedro Ramírez"].randomElement()!
        let emailSuffix = String((0..<8).map { _ in "abcdefghijklmnopqrstuvwxyz0123456789".randomElement()! })
        let email = "dev_\(emailSuffix)@conekta.com"
        let phone = "55\(Int.random(in: 10000000...99999999))"
        let productName = ["Box of Cohiba S1s", "Laptop Pro 15", "Wireless Headphones",
                           "Coffee Maker", "Running Shoes", "Smart Watch"].randomElement()!

        let body: [String: Any] = [
            "three_ds_mode": "strict",
            "currency": "MXN",
            "customer_info": [
                "name": customerName,
                "email": email,
                "phone": phone,
                "corporate": false
            ],
            "line_items": [
                [
                    "name": productName,
                    "unit_price": unitPrice,
                    "quantity": quantity
                ]
            ],
            "checkout": [
                "allowed_payment_methods": ["cash", "card", "bank_transfer", "bnpl", "pay_by_bank"],
                "type": "Integration"
            ]
        ]

        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        do {
            let (data, _) = try await URLSession.shared.data(for: request)
            if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
               let checkout = json["checkout"] as? [String: Any],
               let checkoutId = checkout["id"] as? String {
                return checkoutId
            }
        } catch {
            print("Failed to fetch checkout request ID: \(error)")
        }
        return nil
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
