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
            checksum: "7a1d5de61686985e7da28e7660bceff5e24393e3164e5a63f2d975a9b96d57fd"
        ),
    ]
)
