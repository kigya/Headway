import SwiftUI
import GoogleSignIn
import shared
import Firebase
import UIKit

private let headwayGoogleServerClientId =
    "658272377808-alf63nc9km4tjgv0dr6lltfqfo1jshqb.apps.googleusercontent.com"

private let headwayGoogleIosClientId =
    "658272377808-d617os8k1vmam7rul9fnamj32cbsebaa.apps.googleusercontent.com"

private let headwayGoogleSignInNsErrorDomain = "com.google.GIDSignIn"

private let headwayGoogleSignInCanceledErrorCode: Int = -5

@main
struct HeadwayIOS: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        FirebaseApp.configure()
        IosGoogleSignInBridgeKt.headwayIosRegisterGoogleSignInRunner {
            DispatchQueue.main.async {
                headwayPresentGoogleSignInFromKotlin()
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { url in
                GIDSignIn.sharedInstance.handle(url)
            }
        }
    }
}

private func headwayPresentGoogleSignInFromKotlin() {
    guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
        IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInUnavailable()
        return
    }
    let root = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        ?? scene.windows.first?.rootViewController
    guard let presenter = root else {
        IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInUnavailable()
        return
    }
    let configuration = GIDConfiguration(
        clientID: headwayGoogleIosClientId,
        serverClientID: headwayGoogleServerClientId,
    )
    GIDSignIn.sharedInstance.configuration = configuration
    GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
        if let error {
            let nsError = error as NSError
            if nsError.domain == headwayGoogleSignInNsErrorDomain,
               nsError.code == headwayGoogleSignInCanceledErrorCode {
                IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInUserCancelled()
            } else {
                IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInUnavailable()
            }
            return
        }
        guard let token = result?.user.idToken?.tokenString, !token.isEmpty else {
            IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInUnavailable()
            return
        }
        IosGoogleSignInBridgeKt.headwayIosOnGoogleSignInSuccess(idToken: token)
    }
}
