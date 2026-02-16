@template.layout.index(title = "Home", content = @`
    <div xmlns:data-on="http://www.w3.org/1999/xhtml">
        <h1>Home</h1>
        <p></p>
        <button data-on:click="@get('/clock')">Get time</button>
        <div id="clock"></div>
    </div>
`)