package com.sk.directudhar.ui.applyloan

class ServiceProvide {
    companion object {
        fun getServiceProvider():ArrayList<ServiceProvider>{
            val mList :ArrayList<ServiceProvider> = ArrayList()
            mList.add(ServiceProvider(0,"None","None"))
            mList.add(ServiceProvider(1,"AJMER","Ajmer Vidhyut Vitran Nigam Ltd (AJMER)"))
            return mList
        }
    }
}