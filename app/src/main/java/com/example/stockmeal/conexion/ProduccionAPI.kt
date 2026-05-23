import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.modelos.ProduccionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProduccionAPI {

    @GET("produccion")
    suspend fun obtenerProduccion(): List<Produccion>

    @GET("produccion/fecha/{fecha}")
    suspend fun obtenerProduccionPorFecha(
        @Path("fecha") fecha: String
    ): List<Produccion>

    @GET("produccion/rango")
    suspend fun obtenerProduccionPorRango(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String
    ): List<Produccion>

    @GET("produccion/plato/{idProducto}")
    suspend fun obtenerProduccionPorPlato(
        @Path("idProducto") idProducto: Int
    ): List<Produccion>

    @POST("produccion")
    suspend fun registrarProduccion(
        @Body produccionRequest: ProduccionRequest
    ): Produccion
}