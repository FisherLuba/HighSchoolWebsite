class Categories extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categories: null,
            selectedCategory: null,
            subtopics: [],
            currentAddingSubtopic: null,
            saving: false,
            badRequest: false,
            isExistingCategory: false,
            newCategories: false
        };
    }

    componentDidMount() {
        fetch('/api/categories', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN')
            }
        }).then(response => response.json()).then(data => this.setState({
            categories: data
        }));
    }

    fetchSubtopics() {
        fetch(`/api/category/subtopics?category=${this.state.selectedCategory}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => this.setState({
            subtopics: data
        }));
    }

    sendCategory() {
        fetch(`/api/category/edit?category=${this.state.selectedCategory.toLowerCase()}&subtopics=${this.state.subtopics.join()}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.text()).then(data => {
            this.state.newCategories = true;
            this.displaySaved();
        });
    }

    deleteCategory() {
        fetch(`/api/category/delete?category=${this.state.selectedCategory.toLowerCase()}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.text()).then(data => {
            this.state.newCategories = true;
            this.state.selectedCategory = null;
            this.setState({
                categories: this.state.categories.filter(item => this.state.selectedCategory !== item)
            });
            this.displaySaved();
        });
    }

    displaySaved() {
        this.setState({
            saving: true
        });
        setTimeout(() => {
            this.setState({
                saving: false,
                badRequest: false
            });
        }, this.state.badRequest ? 4000 : 1500);
    }

    render() {
        if (this.state.categories == null) return null;
        const newCategories = this.state.newCategories;
        if (newCategories) this.state.newCategories = false;

        const onSelectCategory = event => {
            this.state.selectedCategory = event.target.dataset.category;
            this.state.isExistingCategory = true;
            this.fetchSubtopics();
        };

        const removeSubtopic = event => {
            const subtopic = event.target.dataset.subtopic;
            const subtopics = this.state.subtopics;
            this.setState({
                subtopics: subtopics.filter(item => item !== subtopic)
            });
        };

        const listItems = [];

        for (let i = 0; i < this.state.subtopics.length; i++) {
            const subtopic = this.state.subtopics[i];
            const listItem = /*#__PURE__*/React.createElement("div", {
                className: "input-group mb-3",
                key: subtopic
            }, /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("input", {
                type: "text",
                readOnly: true,
                className: "form-control",
                key: subtopic,
                defaultValue: subtopic
            }), /*#__PURE__*/React.createElement("button", {
                className: "btn btn-danger",
                type: "button",
                "data-subtopic": subtopic,
                onClick: removeSubtopic
            }, "\u232B")));
            listItems.push(listItem);
        }

        const onAddSubtopic = () => {
            if (this.state.currentAddingSubtopic == null || this.state.currentAddingSubtopic.replace(" ", "") === "" || this.state.subtopics.includes(this.state.currentAddingSubtopic.toLowerCase())) return;
            const subtopics = this.state.subtopics;
            subtopics.push(this.state.currentAddingSubtopic.toLowerCase());
            this.setState({
                subtopics: subtopics
            });
        };

        const onChangeAddSubtopic = event => {
            this.state.currentAddingSubtopic = event.target.value;
        };

        const addSubtopicsInput = /*#__PURE__*/React.createElement("div", {
            className: "input-group mb-3",
            key: "add-subtopic"
        }, /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("input", {
            type: "text",
            className: "form-control",
            placeholder: "Add subtopic",
            onChange: onChangeAddSubtopic
        }), /*#__PURE__*/React.createElement("button", {
            className: "btn btn-primary",
            type: "button",
            onClick: onAddSubtopic
        }, "Add")));
        listItems.push(addSubtopicsInput);

        const onClickSave = () => {
            if (this.state.selectedCategory == null) return;
            this.sendCategory();
        };

        const onClickDelete = () => {
            if (this.state.selectedCategory == null) return;
            this.deleteCategory();
        };

        const alertColor = this.state.badRequest ? "alert-danger" : "alert-success";
        const alertText = this.state.badRequest ? "The data was not saved, please make sure you entered the emails correctly or checked the default permissions box" : "Saved!";

        const onEditAddCategory = event => {
            // if (
            //     this.state.selectedCategory != null &&
            //     this.state.selectedCategory.replace(" ", "") !== ""
            //     && event.target.value.replace(" ", "") !== "" &&
            //     !this.state.isExistingCategory
            // ) return;
            if (this.state.isExistingCategory) this.state.subtopics = [];
            this.state.isExistingCategory = false;
            this.setState({
                selectedCategory: event.target.value
            });
        };

        return /*#__PURE__*/React.createElement("div", {
            className: "justify-content-center vertical-margin"
        }, /*#__PURE__*/React.createElement(React.Fragment, null, this.state.saving && /*#__PURE__*/React.createElement("div", {
            className: alertColor,
            role: "alert"
        }, alertText), /*#__PURE__*/React.createElement(CategoryList, {
            newCategories: newCategories,
            className: "vertical-margin",
            onClick: onSelectCategory
        }), /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center vertical-margin"
        }, /*#__PURE__*/React.createElement("input", {
            placeholder: "Add category...",
            onChange: onEditAddCategory
        })), this.state.selectedCategory != null && this.state.selectedCategory.replace(" ", "") !== "" && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("h3", {
            className: "text-center vertical-margin"
        }, this.state.selectedCategory.charAt(0).toUpperCase() + this.state.selectedCategory.slice(1)), /*#__PURE__*/React.createElement("div", {
            className: "justify-content-center vertical-margin"
        }, /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center"
        }, listItems)), /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center"
        }, /*#__PURE__*/React.createElement("button", {
            type: "button",
            className: "btn btn-danger",
            onClick: onClickDelete
        }, "Delete Category")), /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center"
        }, /*#__PURE__*/React.createElement("button", {
            type: "button",
            className: "btn btn-primary",
            onClick: onClickSave
        }, "Save")))));
    }

}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render( /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Navbar, null), /*#__PURE__*/React.createElement(Categories, null)));