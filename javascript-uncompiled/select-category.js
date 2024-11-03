const root = ReactDOM.createRoot(document.getElementById("root"));
const onClick = event => {
    document.location = `view-category?category=${event.target.dataset.category}`
}
root.render(
    <React.Fragment>
        <Navbar/>
        <CategoryList onClick={onClick}/>
    </React.Fragment>
);