package com.seda.pokemon


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import com.seda.pokemon.remote.responses.Stat
import com.seda.pokemon.remote.responses.Type

import com.seda.pokemon.ui.theme.*
import java.util.*

fun PokemonParse(type: Type): Color {

    return when(type.type.name.toLowerCase(Locale.ROOT)){
        "normal" -> TypeNormal
        "fire"-> TypeFire
        "water"-> TypeWater
        "electric"-> TypeElectric
        "grass"-> TypeGrass
        "ice"-> TypeIce
        "fighting"->TypeFighting
        "poison"->TypePoison
        "ground"->TypeGround
        "flying"->TypeFlying
        "psychic"->TypePsychic
        "bug"->TypeBug
         "rock"->TypeRock
        "ghost"->TypeGhost
        "dragon"->TypeDragon
        "dark"->TypeDark
        "steel"->TypeSteel
        "fairy"->TypeFairy
        else->Color.Black
    }
}

fun parseStateToColor(stat: Stat):Color{

    return when(stat.stat.name.toLowerCase()){
        "hp"->HPColor
         "attack"->AtkColor
         "defence"->DefColor

        "special-attack"->SpAtkColor
         "special-defense"->SpDefColor
         "speed"->SpdColor
         else->Color.White
    }
}
fun parseStatToAbbr(stat: Stat):String{

    return when(stat.stat.name){
        "hp"->"HP"
        "attack"->"Atk"
        "defense"->"Def"
        "special-attack"->"SpAtk"
        "special-defense"->"SpDef"
        "speed"->"Spd"
        else->""
    }

}