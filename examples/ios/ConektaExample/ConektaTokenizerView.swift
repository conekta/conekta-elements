import SwiftUI
import UIKit
import composeKit

struct ConektaTokenizerView: UIViewControllerRepresentable {
    let config: TokenizerConfig
    let onSuccess: (TokenizerResult) -> Void
    let onError: (TokenizerError) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return ConektaTokenizerViewControllerKt.ConektaTokenizerViewController(
            config: config,
            onSuccess: onSuccess,
            onError: onError
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
