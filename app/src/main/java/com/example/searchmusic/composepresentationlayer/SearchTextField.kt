package com.example.searchmusic.composepresentationlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.searchmusic.R
import com.example.searchmusic.composepresentationlayer.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {},
    withIcon: Boolean = false,
    autoFocus: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    ),
    hint: String = stringResource(R.string.query_hint),
    maxLength: Int = 200,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Search,
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences
    ),
    keyboardActions: KeyboardActions = KeyboardActions(onSearch = { onSearch() }),
) {
    val focusRequester = remember { FocusRequester() }
    DisposableEffect(autoFocus) {
        if (autoFocus) focusRequester.requestFocus()
        onDispose { }
    }

    OutlinedTextField(
        value = value,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.9f),
        ),
        leadingIcon = if (withIcon) {
            { SearchTextFieldIcon() }
        } else null,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        placeholder = { Text(text = hint, style = textStyle) },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 8.dp)
            .background(Purple80, MaterialTheme.shapes.small)
            .focusRequester(focusRequester)
    )
}

@Composable
fun SearchTextFieldIcon() {
    Icon(
        tint = MaterialTheme.colorScheme.onBackground,
        imageVector = Icons.Default.Search,
        contentDescription = null
    )
}
