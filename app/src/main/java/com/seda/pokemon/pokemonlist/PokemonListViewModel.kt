package com.seda.pokemon.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.seda.pokemon.Constants.PAGE_SIZE
import com.seda.pokemon.remote.models.PokedexList
import com.seda.pokemon.repository.PokemonRepository
import com.seda.pokemon.repository.Resource



import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(private val repository: PokemonRepository
) :ViewModel() {

    private var curPage = 0
    var pokemonList = mutableStateOf<List<PokedexList>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    var textSearch = mutableStateOf("")

    private var cachedPokemonList = listOf<PokedexList>()
    private  var isSearchStarting = true
    var isSearching = mutableStateOf(false)




    init {
        loadPokemonPaginated()
    }

fun searchPokemonList(query:String){
    textSearch.value = query
    val listToSearch = if (isSearchStarting){

        pokemonList.value
    }else{
        cachedPokemonList
    }

    viewModelScope.launch(Dispatchers.Default) {
        if(query.isEmpty()){
          pokemonList.value = cachedPokemonList
            isSearching.value = false
            isSearchStarting = true
            return@launch
        }
        val results = listToSearch.filter {
             it.pokemonName.contains(query.trim(),ignoreCase = true) ||
                     it.number.toString() == query.trim()
        }

        if (isSearchStarting){
            cachedPokemonList = pokemonList.value
             isSearchStarting = false
        }

        pokemonList.value=results
        isSearching.value=true
    }
}



    fun loadPokemonPaginated(){

        viewModelScope.launch {

            isLoading.value =true
            val result =  repository.getPokemonList(PAGE_SIZE,curPage * PAGE_SIZE)


             when(result){
                 is Resource.Success ->{
                     if (result.data != null) {
                     endReached.value = curPage * PAGE_SIZE >= result.data!!.count

                      val pokedexEntries = result.data.results.map { result ->
                          val number = if (result.url.endsWith("/")) {
                              result.url.dropLast(1).takeLastWhile {
                                  it.isDigit()
                              }

                          } else {
                              result.url.takeLastWhile { it.isDigit() }
                          }
                          val url =
                              "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$number.png"
                          PokedexList(result.name.replaceFirstChar {
                              if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString()
                          }, url, number.toInt())
                      }
                          curPage++
                          pokemonList.value += pokedexEntries
                      }

                     loadError.value = ""
                     isLoading.value = false


                 }
                 is Resource.Error ->{
                     loadError.value = result.message!!
                     isLoading.value = false
                 }


                 else -> {}
             }

        }
    }

    fun calcDominantColor(drawable: Drawable,onFinish:(Color)->Unit){
        val bmp =(drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)

        Palette.from(bmp).generate { palette ->
            // Use generated instance
            palette?.dominantSwatch?.rgb?.let {
            onFinish(Color(it))
            }
        }

    }

}