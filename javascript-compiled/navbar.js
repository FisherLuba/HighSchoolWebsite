function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

class Navbar extends React.Component {
    constructor(props) {
        super(props);

        _defineProperty(this, "logout", () => {
            fetch(`/logout`, {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN')
                }
            }).then(response => response.text()).then(data => window.location.href = "/");
            return true;
        });

        this.state = {
            permissions: null
        };
    }

    componentDidMount() {
        fetch(`/api/permissions`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        }).then(response => response.json()).then(data => this.setState({
            permissions: data
        }));
    }

    render() {
        if (this.state.permissions == null) return null;
        return /*#__PURE__*/React.createElement("nav", {
            className: "navbar navbar-expand-sm bg-dark navbar-dark"
        }, /*#__PURE__*/React.createElement("div", {
            className: "container-fluid"
        }, /*#__PURE__*/React.createElement("a", {
            className: "navbar-brand",
            href: "/view-category?category=recent"
        }, /*#__PURE__*/React.createElement("img", {
            className: "img-fluid",
            alt: "Lynnfield schools logo",
            src: "/images/logo.png"
        })), /*#__PURE__*/React.createElement("button", {
            className: "navbar-toggler",
            type: "button",
            "data-bs-toggle": "collapse",
            "data-bs-target": "#collapsibleNavbar"
        }, /*#__PURE__*/React.createElement("span", {
            className: "navbar-toggler-icon"
        })), /*#__PURE__*/React.createElement("div", {
            className: "collapse navbar-collapse",
            id: "collapsibleNavbar"
        }, /*#__PURE__*/React.createElement("ul", {
            className: "navbar-nav"
        }, this.state.permissions["post_question"] && /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "submit-question"
        }, "Submit Question")), (this.state.permissions["edit_user_permissions"] || this.state.permissions["edit_default_permissions"]) && /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "edit-permissions"
        }, "Edit Permissions")), /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "select-category"
        }, "View Categories")), /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "/view-category?category=recent"
        }, "View Most Recent")), this.state.permissions["approve_post"] && /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "/view-category?category=unapproved"
        }, "View Unapproved")), this.state.permissions["edit_categories"] && /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link",
            href: "/edit-categories"
        }, "Edit Categories")), /*#__PURE__*/React.createElement("li", {
            className: "nav-item"
        }, /*#__PURE__*/React.createElement("a", {
            className: "nav-link pointer-on-hover",
            onClick: this.logout
        }, "Logout"))))));
    }

}