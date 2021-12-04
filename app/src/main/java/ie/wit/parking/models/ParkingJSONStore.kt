package ie.wit.parking.models


import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ie.wit.parking.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE_PARKING = "parking.json"
const val JSON_FILE_USER = "user.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listTypeParking: Type = object : TypeToken<ArrayList<ParkingModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ParkingJSONStore(private val context: Context) : ParkingStore {

    var parkings = mutableListOf<ParkingModel>()

    init {
        if (exists(context, JSON_FILE_PARKING)) {
            deserialize()
        }
        if (exists(context, JSON_FILE_USER)) {
            deserializeUsers()
        }
    }

    //PARKINGS
    override fun findAll(): MutableList<ParkingModel> {
        logAllParkings()
        return parkings
    }
    override fun findParkingById(id:Long): ParkingModel? {
        var foundParking: ParkingModel? = parkings.find { p -> p.id == id }
        if (foundParking != null) {
            return foundParking
        }
        return null
    }

    override fun findParkingByUsername(username:String): MutableList<ParkingModel>? {
        val userParking = parkings.filter { p -> p.username == username }
        return userParking.toMutableList()
    }

    override fun create(parking: ParkingModel) {
        parking.id = generateRandomId()
        parkings.add(parking)
        serialize()
    }


    override fun update(parking: ParkingModel) {
        val parkingsList = findAll() as ArrayList<ParkingModel>
        var foundParking: ParkingModel? = parkingsList.find { p -> p.id == parking.id }
        if (foundParking != null) {
            foundParking.title = parking.title
            foundParking.description = parking.description
            foundParking.image = parking.image
            foundParking.lat = parking.lat
            foundParking.lng = parking.lng
            foundParking.zoom = parking.zoom
            foundParking.rating = parking.rating
        }
        serialize()
    }

    override fun delete(parking: ParkingModel) {
        parkings.remove(parking)
        serialize()
    }


    private fun logAllParkings() {
        parkings.forEach { Timber.i("$it") }
    }

    //USER
    override fun findByUsername(username: String):UserModel? {
        var foundUser: UserModel? = users.find { p -> p.username == username }
        if (foundUser != null) {
            return foundUser
        }
        return null
    }
    override fun create(user: UserModel):Boolean {
        var foundUser: UserModel? = users.find { p -> p.username == user.username }
        if (foundUser == null) {
            users.add(user)
            logAllUsers()
            serializeUsers()
            return true
        }else{
            return false
        }


    }
    override fun update(user: UserModel) {
        var foundUser: UserModel? = users.find { p -> p.username == user.username }
        if (foundUser != null) {
            foundUser.username = user.username
            foundUser.password = user.password
            logAllUsers()
        }
        serializeUsers()
    }
    override fun delete(user: UserModel) {
        var foundUser: UserModel? = users.find { p -> p.username == user.username }
        if (foundUser != null) {
            users.removeAt(users.indexOf(foundUser))
            logAllUsers()
        }
        serializeUsers()
    }
    override fun authenticate(user: UserModel) :UserModel? {
        var foundUser: UserModel? = users.find { p -> p.username == user.username }
        if (foundUser != null) {
            if(foundUser.password == user.password){
                Timber.i("User: ${user.username} Authenticated")
                return foundUser;
            }
        }
        Timber.i("User: ${user.username} Rejected")
        return foundUser;
    }
    fun logAllUsers() {
        users.forEach{ Timber.i("${it}") }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(parkings, listTypeParking)
        write(context, JSON_FILE_PARKING, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE_PARKING)
        parkings = gsonBuilder.fromJson(jsonString, listTypeParking)
    }

    private fun serializeUsers() {
        val jsonString = gsonBuilder.toJson(users, listTypeUser)
        write(context, JSON_FILE_USER, jsonString)
    }

    private fun deserializeUsers() {
        val jsonString = read(context, JSON_FILE_USER)
        users = gsonBuilder.fromJson(jsonString, listTypeUser)
    }


}



class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}