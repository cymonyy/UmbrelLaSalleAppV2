
package com.mobdeve.s15.nadela.oliva.quizon.myapplication.models

class StationModel {

    var id: String = ""
    var admins: MutableList<String> = mutableListOf()
    var stock: MutableList<StockItemModel> = mutableListOf()
    var existingTransactions: MutableList<TransactionModel> = mutableListOf()
    var name: String = ""

    constructor()
    constructor(
        id: String,
        name: String,
        admins: MutableList<String>,
        stock: MutableList<StockItemModel>,
        existingTransactions: MutableList<TransactionModel>
    ) {
        this.id = id
        this.name = name
        this.admins = admins
        this.stock = stock
        this.existingTransactions = existingTransactions
    }


}