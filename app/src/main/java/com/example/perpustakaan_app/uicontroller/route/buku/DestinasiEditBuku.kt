package com.example.perpustakaan_app.uicontroller.route.buku

object DestinasiEditBuku: DestinasiNavigasiBuku {
    override val route = "edit_buku"
    override val tittleRes =  "Edit Buku"
    const val idBukuArg = "id_buku"

    val routeWithArgs = "$route/{$idBukuArg}"
}