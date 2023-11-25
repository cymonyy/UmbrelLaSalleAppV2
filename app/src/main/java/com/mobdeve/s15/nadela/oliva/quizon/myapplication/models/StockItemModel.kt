
package com.mobdeve.s15.nadela.oliva.quizon.myapplication.models

class StockItemModel {

    var itemCategory: String = ""
    var available: Int = 0
    var totalStock : Int = 0
    var borrowedCount : Int = 0

    constructor(itemCategory: String, available: Int, totalStock: Int, borrowedCount: Int){
        this.itemCategory = itemCategory
        this.available = available
        this.totalStock = totalStock
        this.borrowedCount = borrowedCount
    }

    constructor()


}