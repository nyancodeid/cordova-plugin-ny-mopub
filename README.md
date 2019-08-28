# NyanMoPub (only Interstitial)
### MoPub Cordova Plugin for Android Only

```js
var EXAMPLE_INTERSTITIAL_ID = "24534e1901884e398f1253216226017e"

cordova.plugins.NyanMoPub.initialize(EXAMPLE_INTERSTITIAL_ID);

cordova.plugins.NyanMoPub.load("test"); // run `load` test using test id
cordova.plugins.NyanMoPub.load("interstitial", YOUR_INTERSTITIAL_ID);

cordova.plugins.NyanMoPub.show("test");
cordova.plugins.NyanMoPub.show("interstitial");
```

```js
document.addEventListener("onInterstitialLoaded", console.log)
document.addEventListener("onInterstitialFailed", console.log)
document.addEventListener("onInterstitialShown", console.log)
document.addEventListener("onInterstitialClicked", console.log)
document.addEventListener("onInterstitialDismissed", console.log)
// example result event
// { adNetwork: "MoPub", adType: "interstitial", adEvent: "onInterstitialLoaded", ... }
// { adNetwork: "MoPub", adType: "interstitial", adEvent: "onInterstitialFailed", adData: "No ads found.", ... }
```