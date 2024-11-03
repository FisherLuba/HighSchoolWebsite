const root = ReactDOM.createRoot(document.getElementById("root"));

const onClick = event => {
    document.location = `view-category?category=${event.target.dataset.category}`;
};

root.render( /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Navbar, null), /*#__PURE__*/React.createElement(CategoryList, {
    onClick: onClick
})));