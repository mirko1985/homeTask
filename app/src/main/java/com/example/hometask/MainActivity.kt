package com.example.hometask

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.hometask.viewmodels.CharactersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold {
                MainComposable()
            }
        }
    }
}

@Composable
fun MainComposable(charactersViewModel: CharactersViewModel = hiltViewModel()) {

    val snackbarHostState = remember { SnackbarHostState() }

    val pagingItems = charactersViewModel.charactersInfoList.collectAsLazyPagingItems()

    LazyColumn {
        items(pagingItems.itemCount) {
            pagingItems[it]?.let { it1 -> CharacterItemLayout(characterInfo = it1) }
        }
    }

    pagingItems.apply {
        when {
            loadState.append is LoadState.Error -> {
                showSnackBarError(snackbarHostState, loadState.append as LoadState.Error) {
                    pagingItems.retry()
                }
            }
            loadState.refresh is LoadState.Error -> {
                showSnackBarError(snackbarHostState, loadState.refresh as LoadState.Error) {
                    pagingItems.retry()
                }
            }
        }
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

fun showSnackBarError(
    snackbarHostState: SnackbarHostState,
    loadStateError: LoadState.Error,
    onRetry: () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        val act = snackbarHostState.showSnackbar(
            message = loadStateError.error.message ?: "Unknown error",
            actionLabel = "Retry"
        )

        when (act) {
            SnackbarResult.ActionPerformed -> {
                onRetry()
            }

            else -> {}
        }
    }
}

@Composable
fun CharacterItemLayout(characterInfo: CharactersViewModel.CharacterInfo) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.teal_200)
        ),
        modifier = Modifier
            .padding(5.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (ivImage, tvName, tvSpecies, tvType, tvStatus) = createRefs()

            // ImageView
            AsyncImage(
                model = characterInfo.image,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.FillHeight,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .constrainAs(ivImage) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )

            val textModifier = Modifier
                .constrainAs(tvName) {
                    top.linkTo(parent.top)
                    start.linkTo(ivImage.end, margin = 16.dp)
                }

            Text(
                text = stringResource(R.string.name, characterInfo.name),
                modifier = textModifier,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                text = stringResource(R.string.species, characterInfo.species),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(tvSpecies) {
                        top.linkTo(tvName.bottom)
                        start.linkTo(tvName.start)
                    }
            )

            Text(
                text = stringResource(R.string.type, characterInfo.type.ifEmpty { stringResource(R.string.unknown) }),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .constrainAs(tvType) {
                        top.linkTo(tvSpecies.bottom)
                        start.linkTo(tvName.start)
                    }
            )

            Text(
                text = stringResource(R.string.status, characterInfo.status),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .constrainAs(tvStatus) {
                        top.linkTo(tvType.bottom)
                        start.linkTo(tvName.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }

}