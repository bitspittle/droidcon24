## CssStyle

{{{ Horizontal

```css 1 <html> [css]
/* example.css */
.rect {
  width: 50px;
  height: 25px;
}
```

```html 1 <html> [html]
<!-- example.html -->
<div
   id="example"
   class="rect"
/>
```

}}}

{{{ Horizontal

```kotlin 0 <kobweb> [left]
val RectStyle = CssStyle {
    base {
        Modifier
            .width(50.px)
            .height(25.px)
    }
}
```

```kotlin 2 <kobweb,fragment> [right]
Div(
   RectStyle.toModifier()
     .id("example")
     .toAttrs()
)
```

}}}
