package com.patriq.kronicles.notifications

class Data {
    private var user: String = ""
    private var icon: Int = 0
    private var body: String = ""
    private var title: String = ""
    private var sented: String = ""

    constructor()
    constructor(user: String, icon: Int, body: String, title: String, sent: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }

    fun getUser(): String?{
        return user
    }
    fun setUser(user: String){
        this.user = user
    }
    fun geticon(): Int?{
        return icon
    }
    fun seticon(icon: Int){
        this.icon = icon
    }
    fun getbody(): String?{
        return body
    }
    fun setbody(body: String){
        this.body= body
    }
    fun gettitle(): String?{
        return title
    }
    fun settitle(title: String){
        this.title = title
    }
    fun getsent(): String?{
        return sented
    }
    fun setsent(sent: String){
        this.sented = sented
    }

}