var exec = require('cordova/exec');

function NyanMoPub() {
}

NyanMoPub.prototype.exec = function(eventName, eventData) {
    return new Promise(function(resolve, reject) {
        exec(resolve, reject, 'NyanMoPub', eventName, eventData);
    });
}

NyanMoPub.prototype.initialize = function (appId) {
    return this.exec("initialize", [ appId ]);
};
NyanMoPub.prototype.isLoaded = function (adType) {
    return this.exec("isLoaded", [ adType ]);
};
NyanMoPub.prototype.load = function (adType, adId) {
    return this.exec('load', [ adType, adId ]);
};
NyanMoPub.prototype.show = function (adType) {
    return this.exec('show', [ adType ]);
};

module.exports = new NyanMoPub();
