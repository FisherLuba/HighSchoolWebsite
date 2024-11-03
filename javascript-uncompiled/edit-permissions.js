class Permissions extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            permissions: null,
            unsavedPermissions: null,
            userPermissions: null,
            users: [],
            defaultPermissions: false,
            saving: false,
            badRequest: false
        };
    }

    componentDidMount() {
        this.fetchPermissions();
    }

    fetchPermissions() {
        let url = `/api/permissions`;
        if (this.state.users.length === 1) {
            url = url + `?userEmail=${this.state.users[0]}`;
        }
        fetch(url, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        })
            .then(response => response.json())
            .then(data => this.setState({
                permissions: data,
                unsavedPermissions: Object.assign({}, data)
            }));


        fetch(`/api/permissions`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'text/plain'
            }
        })
            .then(response => response.json())
            .then(data => this.setState({
                userPermissions: data
            }));
    }

    getPermissionsAsString() {
        let str = "";
        let length = 0;
        for (let [k, v] of Object.entries(this.state.unsavedPermissions)) {
            if (length !== 0) {
                str += ",";
            }
            length = length + 1;
            str += `${k}:${v}`
        }
        return str;
    }

    updatePermissions() {
        let url = `/api/set-permissions?default-permissions=${this.state.defaultPermissions}&permissions=${this.getPermissionsAsString()}`;
        // if (this.state.users.length === 1) {
        //     url = url + `?userEmail=${this.state.users[0]}`;
        // }
        fetch(url, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify(this.state.users)
        })
            .then(response => response.status)
            .then(status => {
                if (status === 200) {
                    this.displaySaved();
                    this.state.permissions = this.state.unsavedPermissions;
                    return;
                }
                this.state.badRequest = true;
                this.displaySaved();
            });
    }

    displaySaved() {
        this.setState({saving: true});
        setTimeout(() => {
            this.setState({
                saving: false,
                badRequest: false
            });
        }, (this.state.badRequest ? 4000 : 1500))
    }

    render() {
        if (this.state.userPermissions == null || this.state.permissions == null) {
            return null;
        }
        const canEditDefault = this.state.userPermissions["edit_default_permissions"];
        const canEditUsers = this.state.userPermissions["edit_user_permissions"];
        if (!canEditDefault && !canEditUsers) {
            window.location.href = "/";
            return null;
        }

        const handlePermissionCheckboxClick = event => {
            const permission = event.target.dataset.permission;
            this.state.unsavedPermissions[permission] = event.target.checked;
        }

        const onEnterEmail = event => {
            this.state.users = event.target.value.trim().replace(/,/g, " ").split(/\s+/);
            if (this.state.users.length === 1 && this.state.users[0].length === 0) this.state.users = [];
        }

        const onClickSave = () => {
            this.updatePermissions();
        }

        const handleDefaultPermissionsClick = event => {
            this.state.defaultPermissions = event.target.checked;
        }

        const alertColor = (this.state.badRequest ? "alert-danger" : "alert-success");
        const alertText = (this.state.badRequest ? "The data was not saved, please make sure you entered the emails correctly or checked the default permissions box" : "Saved!");

        return (
            <React.Fragment>
                <form>
                    <div className="container mt-3 text-center">
                        {this.state.saving &&
                        <div className={alertColor} role="alert">
                            {alertText}
                        </div>
                        }
                        <React.Fragment>
                            <div className="row justify-content-center vertical-margin question-snapshot">
                                <div className={"col col-md-6 text-center"}>
                                    {Object.keys(this.state.unsavedPermissions).map(permission => {
                                            const value = this.state.unsavedPermissions[permission];
                                            let className = "checkbox-dropdown-label";
                                            if (permission === "administrator" && !this.state.userPermissions["administrator"]) {
                                                className += " disabled";
                                            }
                                            const disabled = permission === "administrator" && !this.state.userPermissions["administrator"];
                                            return (<React.Fragment key={permission}>
                                                <div className={"col col-md-6 text-center"}>
                                                    <label
                                                        className={className}>{permission}</label><input
                                                    defaultChecked={value}
                                                    data-permission={permission}
                                                    onClick={handlePermissionCheckboxClick}
                                                    type="checkbox"
                                                    disabled={disabled}
                                                />
                                                </div>
                                            </React.Fragment>)
                                        }
                                    )}
                                </div>
                            </div>

                            {canEditUsers &&
                            <div className="row justify-content-center vertical-margin question-snapshot">
                                <div className={"col col-md-6"}>
                                    <label>Enter the emails here, they can be separated by spaces or commas</label>
                                    <textarea className={"form-control"} placeholder={"Enter emails here"}
                                              onChange={onEnterEmail}/>
                                </div>
                            </div>
                            }

                            {canEditDefault &&
                            <React.Fragment>
                                <label
                                    className="checkbox-dropdown-label">Update Default Permissions</label><input
                                defaultChecked={false}
                                onClick={handleDefaultPermissionsClick}
                                type="checkbox"/>
                            </React.Fragment>
                            }

                            <div className="row justify-content-center vertical-margin question-snapshot">
                                <div className={"col col-md-6"}>
                                    <button type={"button"} onClick={onClickSave}>Save Permissions</button>
                                </div>
                            </div>
                        </React.Fragment>
                    </div>
                </form>
            </React.Fragment>
        );
    }
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
    <React.Fragment>
        <Navbar/>
        <Permissions/>
    </React.Fragment>
);