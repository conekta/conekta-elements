// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "ConektaElements",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "composeKit", targets: ["composeKit"]),
    ],
    targets: [
        .binaryTarget(
            name: "composeKit",
            url: "https://github.com/conekta/conekta-elements/releases/download/latest/composeKit.xcframework.zip",
            checksum: "e5fc1a7030756eea3f8cc4c229c178baac9369e4c3ba3238274f03824fdb02b2"
        ),
    ]
)
