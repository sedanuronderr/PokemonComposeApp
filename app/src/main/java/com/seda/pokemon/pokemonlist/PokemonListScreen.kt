package com.seda.pokemon.pokemonlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.*
import coil.request.ImageRequest
import com.seda.pokemon.R
import com.seda.pokemon.pokemonlist.PokemonListViewModel
import com.seda.pokemon.remote.models.PokedexList
import com.seda.pokemon.ui.theme.RobotoCondensed
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@Composable
fun PokemonListScreen(navController: NavController,viewModel: PokemonListViewModel= hiltViewModel()){
       Surface(
           color = MaterialTheme.colors.background,
           modifier = Modifier.fillMaxSize()
       ) {
           Column {
               Spacer(modifier = Modifier.height(20.dp))
              Image(painter = painterResource(id = R.drawable.ic_international_pok_mon_logo) ,
                  contentDescription ="Pokemon",
                  modifier = Modifier
                      .fillMaxWidth()
                      .align(CenterHorizontally)
              )

               SearchBar(
                      hint = "Search",
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(16.dp)
               ){
                   viewModel.searchPokemonList(it)
               }
               Spacer(modifier = Modifier.height(16.dp))

     PokemonList(navController = navController)
           }
   }
}

@Composable
fun SearchBar(
    modifier: Modifier=Modifier,
    hint:String="",
    onSearch:(String)->Unit={}
){
   var text by remember{
    mutableStateOf("")
   }
    var isHintDisplayed by remember{
        mutableStateOf(hint!="")
    }
    Box(modifier = modifier){
        BasicTextField(
            value = text,
            onValueChange = {
            text = it
            onSearch(it)
        },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it.isFocused != true
                }
            )
        if(isHintDisplayed){
        Text(
            text = hint,
            color = Color.LightGray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }

    }




}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
){
    val pokemonList by remember{viewModel.pokemonList}
    val endReached by remember {viewModel.endReached}
    val loadError by remember {viewModel.loadError}
    val isLoading by remember {viewModel.isLoading}
    val isSearching by remember {viewModel.isSearching}

    LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
        val itemCount = if(pokemonList.size % 2 == 0 ){
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1

        }

        items(itemCount){
            if((it >= (itemCount - 1)) && !endReached && !isLoading && !isSearching){

                viewModel.loadPokemonPaginated()
            }

           // PokedexRow(rowIndex = it, entries = pokemonList, navController = navController )
            PokedexEntry(entry = pokemonList[it], navController =navController )
        }
    })


}
//Box tasarımı
@Composable
fun PokedexEntry(
    entry: PokedexList,
    navController: NavController,
    modifier: Modifier=Modifier,
    viewModel: PokemonListViewModel= hiltViewModel()

    ) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box( contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(0.dp, RoundedCornerShape(10.dp))
            .padding(12.dp)
            .clip(RoundedCornerShape(14.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}")
            }) {
        Column {


         val painter= rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)

                    .build(),

            )
            val state = painter.state

            Image(painter = painter, contentDescription = entry.pokemonName, modifier = Modifier.size(120.dp).align(CenterHorizontally))

            if (state is AsyncImagePainter.State.Success) {
                viewModel.calcDominantColor(state.result.drawable) { color ->
                    dominantColor = color
                }
            }
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }

}



