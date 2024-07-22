package com.example.fibertel.network

import com.example.fibertel.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * ApiService define los endpoints de la API que la aplicación necesita consumir.
 * Cada función en esta interfaz representa una solicitud HTTP específica que se
 * realizará utilizando Retrofit.
 */
interface ApiService {

    /**
     * Realiza una solicitud HTTP GET al endpoint "clients/{dni}" para obtener
     * los datos de un usuario específico basado en su DNI.
     *
     * @param dni El DNI del usuario que queremos consultar.
     * @return Un objeto Call que representa la solicitud HTTP que se puede
     *         ejecutar de forma asíncrona o síncrona. La respuesta esperada
     *         es un objeto User.
     */
    @GET("clients/{dni}")
    fun getUserData(@Path("dni") dni: String): Call<User>


    // Añade aquí otros endpoints necesarios
}
