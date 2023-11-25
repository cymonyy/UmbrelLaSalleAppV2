package com.mobdeve.s15.nadela.oliva.quizon.myapplication.models
class ItemModel {

    var id: String = ""
    var borrowed: Boolean = false
    var status: String = "Intact"
    var requested: Boolean = false
    var itemCategory: String = ""
    var station: String = ""

    constructor(id: String, borrowed: Boolean, status: String, requested: Boolean) {
        this.id = id
        this.borrowed = borrowed
        this.status = status
        this.requested = requested
    }

    constructor(borrowed: Boolean, status: String, requested: Boolean, itemCategory: String, station: String) {
        this.borrowed = borrowed
        this.status = status
        this.requested = requested
        this.itemCategory = itemCategory
        this.station = station
    }






}