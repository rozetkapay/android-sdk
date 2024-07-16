@file:OptIn(ExperimentalComposeUiApi::class)

package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.theme.DomainTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FormTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onTextLayout: (TextLayoutResult) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = RoundedCornerShape(DomainTheme.sizes.componentCornerRadius),
) {
    val colors = textFiledColors()
    val textColor = if (isError) DomainTheme.colors.error else DomainTheme.colors.onComponent
    val mergedTextStyle = DomainTheme.typography.input
        .merge(TextStyle(color = textColor))

    Column(
        modifier = modifier,
    ) {
        BasicTextField(
            value = value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = false,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(textColor),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = if (placeholder.isNotBlank()) {
                        @Composable {
                            Text(
                                text = placeholder,
                                style = DomainTheme.typography.input,
                                color = DomainTheme.colors.placeholder,
                            )
                        }
                    } else {
                        null
                    },
                    label = null,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 14.dp,
                        top = 14.dp,
                    ),
                    shape = shape
                )
            },
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = DomainTheme.typography.labelSmall,
                color = DomainTheme.colors.error,
                modifier = Modifier
                    .semantics { this.contentDescription = "input-field-helper-text" }
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
internal fun FormTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onTextLayout: (TextLayoutResult) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = RoundedCornerShape(DomainTheme.sizes.componentCornerRadius),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    var lastTextValue by remember(value) { mutableStateOf(value) }

    FormTextField(
        placeholder = placeholder,
        value = textFieldValue,
        onValueChange = { newTextFieldValueState ->
            textFieldValueState = newTextFieldValueState

            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
            lastTextValue = newTextFieldValueState.text

            if (stringChangedSinceLastInvocation) {
                onValueChange(newTextFieldValueState.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        onTextLayout = onTextLayout,
        visualTransformation = visualTransformation,
        shape = shape,
    )
}

@Composable
private fun textFiledColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = DomainTheme.colors.onComponent,
        unfocusedTextColor = DomainTheme.colors.onComponent,
        disabledTextColor = DomainTheme.colors.onComponent,
        focusedContainerColor = DomainTheme.colors.componentSurface,
        unfocusedContainerColor = DomainTheme.colors.componentSurface,
        errorContainerColor = DomainTheme.colors.componentSurface,
        disabledContainerColor = DomainTheme.colors.componentSurface,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = DomainTheme.colors.onComponent,
        unfocusedLeadingIconColor = DomainTheme.colors.onComponent,
        disabledLeadingIconColor = DomainTheme.colors.onComponent,
        errorLeadingIconColor = DomainTheme.colors.onComponent,
        focusedTrailingIconColor = DomainTheme.colors.onComponent,
        unfocusedTrailingIconColor = DomainTheme.colors.onComponent,
        disabledTrailingIconColor = DomainTheme.colors.onComponent,
        errorTrailingIconColor = DomainTheme.colors.onComponent,
        focusedLabelColor = DomainTheme.colors.placeholder,
        unfocusedLabelColor = DomainTheme.colors.placeholder,
        disabledLabelColor = DomainTheme.colors.placeholder,
        errorLabelColor = DomainTheme.colors.placeholder,
        focusedPlaceholderColor = DomainTheme.colors.placeholder,
        unfocusedPlaceholderColor = DomainTheme.colors.placeholder,
        disabledPlaceholderColor = DomainTheme.colors.placeholder,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TextFieldIcon(
    painter: Painter,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    Icon(
        modifier = Modifier
            .size(20.dp)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClickLabel = "Button action description",
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        painter = painter,
        tint = tint,
        contentDescription = contentDescription,
    )
}

@Composable
@Preview(
    showBackground = false,
)
private fun FormTextFieldsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            placeholder = "Placeholder",
            onValueChange = {},
        )
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            placeholder = "Placeholder",
            onValueChange = {},
            trailingIcon = {
                TextFieldIcon(
                    painter = painterResource(id = R.drawable.ic_close),
                    tint = Color.Gray
                )
            }
        )
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "Value",
            placeholder = "Placeholder",
            onValueChange = {},
        )
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "Value",
            placeholder = "Placeholder",
            isError = true,
            errorMessage = "Error message",
            onValueChange = {},
        )
    }
}