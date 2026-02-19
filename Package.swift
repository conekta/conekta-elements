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
            path: "compose/build/XCFrameworks/release/composeKit.xcframework"
        ),
    ]
)
