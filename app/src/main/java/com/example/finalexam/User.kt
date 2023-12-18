package com.example.finalexam

class User {
    var uId: String? = null
    var uEmail: String? = null
    var uName: String? = null
    var uBirth: String? = null
    var uImage: String? = null
    constructor() {

    }
    constructor(uId: String?, uEmail: String?, uName: String?, uBirth: String?, uImage: String?) {
        this.uId = uId
        this.uEmail = uEmail
        this.uName = uName
        this.uBirth = uBirth
        this.uImage = uImage
    }

    constructor(uEmail: String?, uName: String?, uBirth: String?, uImage: String?) {
        this.uEmail = uEmail
        this.uName = uName
        this.uBirth = uBirth
        this.uImage = uImage
    }

}
