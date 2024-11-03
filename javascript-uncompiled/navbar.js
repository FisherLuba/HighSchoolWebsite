class Navbar extends React.Component {

    constructor(props) {
        super(props);
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
        })
            .then(response => response.json())
            .then(data => this.setState({permissions: data}));
    }

    logout = () => {
        fetch(`/logout`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
            }
        })
            .then(response => response.text())
            .then(data => window.location.href = "/");
        return true;
    }

    render() {
        if (this.state.permissions == null) return null;
        return (
            <nav className="navbar navbar-expand-sm bg-dark navbar-dark">
                <div className="container-fluid">
                    <a className="navbar-brand" href="/view-category?category=recent"><img className={"img-fluid"} alt={"Lynnfield schools logo"} src={"/images/logo.png"}/></a>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#collapsibleNavbar">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="collapsibleNavbar">
                        <ul className="navbar-nav">
                            {this.state.permissions["post_question"] &&
                            <li className="nav-item">
                                <a className="nav-link" href="submit-question">Submit Question</a>
                            </li>
                            }
                            {(this.state.permissions["edit_user_permissions"] || this.state.permissions["edit_default_permissions"]) &&
                            <li className="nav-item">
                                <a className="nav-link" href="edit-permissions">Edit Permissions</a>
                            </li>
                            }
                            <li className="nav-item">
                                <a className="nav-link" href="select-category">View Categories</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="/view-category?category=recent">View Most Recent</a>
                            </li>
                            {this.state.permissions["approve_post"] &&
                            <li className="nav-item">
                                <a className="nav-link" href="/view-category?category=unapproved">View Unapproved</a>
                            </li>
                            }
                            {this.state.permissions["edit_categories"] &&
                            <li className="nav-item">
                                <a className="nav-link" href="/edit-categories">Edit Categories</a>
                            </li>
                            }
                            <li className="nav-item">
                                <a className="nav-link pointer-on-hover" onClick={this.logout}>Logout</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
}