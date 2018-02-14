function gidRandom() {
    return "xxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0,
            v = c == "x" ? r : (r & 3 | 8);
        return v.toString(16)
    }).toUpperCase()
}

function  callback() {
    return "bd__cbs__" + Math.floor(2147483648 * Math.random()).toString(36)
}