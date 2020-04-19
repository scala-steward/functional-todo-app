package com.dylanm.functionalTodoApp.module.config

case class DbConfig(
                     url: String,
                     user: String,
                     password: String,
                     minPoolSize: Int,
                     maxPoolSize: Int
                   )
