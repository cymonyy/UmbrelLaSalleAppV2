
package com.mobdeve.s15.nadela.oliva.quizon.myapplication.models

class TransactionModel {

    var id: String = ""
    var borrower: String = ""
    var station: String = ""
    var status: String = ""
    var transactionDate: String = ""
    var expectedReturnDate: String = ""
    var actualReturnDate: String = ""
    var requestedItems: MutableMap<String, String> = mutableMapOf()
    var requestNote: String = ""
    var returnNote: String = ""

    constructor()
    constructor(
        id: String,
        borrower: String,
        station: String,
        status: String,
        transactionDate: String,
        expectedReturnDate: String,
        actualReturnDate: String,
        requestedItems: MutableMap<String, String>,
        requestNote: String,
        returnNote: String
    ) {
        this.id = id
        this.borrower = borrower
        this.station = station
        this.status = status
        this.transactionDate = transactionDate
        this.expectedReturnDate = expectedReturnDate
        this.actualReturnDate = actualReturnDate
        this.requestedItems = requestedItems
        this.requestNote = requestNote
        this.returnNote = returnNote
    }

    constructor(
        borrower: String,
        station: String,
        status: String,
        transactionDate: String,
        expectedReturnDate: String,
        actualReturnDate: String,
        requestedItems: MutableMap<String, String>,
        requestNote: String,
        returnNote: String
    ) {
        this.borrower = borrower
        this.station = station
        this.status = status
        this.transactionDate = transactionDate
        this.expectedReturnDate = expectedReturnDate
        this.actualReturnDate = actualReturnDate
        this.requestedItems = requestedItems
        this.requestNote = requestNote
        this.returnNote = returnNote
    }

}