//
//  ContentView.swift
//  BILIBILIAS
//
//  Created by 萌新杰少 on 2026/5/18.
//

import SwiftUI
import UIKit
import ASShared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

#Preview {
    ContentView()
}
