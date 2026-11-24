import SwiftUI
import WebKit

@main
struct ManhuntApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ManhuntWebView()
            .background(Color.white)
    }
}

struct ManhuntWebView: UIViewRepresentable {
    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.allowsBackForwardNavigationGestures = true
        webView.scrollView.contentInsetAdjustmentBehavior = .never
        webView.load(URLRequest(url: URL(string: "https://manhunt-challenge.floot.app")!))
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}
}