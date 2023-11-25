
package com.mobdeve.s15.nadela.oliva.quizon.myapplication.models

class UserModel{

    var id: String = ""
    var lastName: String = ""
    var firstName: String = ""
    var email: String = ""
    var mobileNumber: String = ""
    var isAdmin: Boolean = false


    constructor() {}
    constructor(lastName: String, firstName: String, email: String, mobileNumber: String) : this() {
        this.id = id
        this.lastName = lastName
        this.firstName = firstName
        this.email = email
        this.mobileNumber = mobileNumber
    }

    constructor(lastName: String, firstName: String, email: String, mobileNumber: String, isAdmin: Boolean) : this() {
        this.id = id
        this.lastName = lastName
        this.firstName = firstName
        this.email = email
        this.mobileNumber = mobileNumber
        this.isAdmin = isAdmin
    }

}