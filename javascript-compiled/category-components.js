class CategoryList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categories: [],
            selectedCategory: null,
            newCategories: false
        };
    }

    componentDidMount() {
        this.getCategories();
    }

    getCategories() {
        fetch('/api/categories', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN')
            }
        }).then(response => response.json()).then(data => this.setState({
            categories: data
        }));
    }

    render() {
        if (this.props.newCategories === true && this.state.newCategories === false) {
            this.state.newCategories = true;
            this.getCategories();
            return null;
        }

        return /*#__PURE__*/React.createElement("div", {
            className: "container mt-3 text-center"
        }, /*#__PURE__*/React.createElement("div", {
            className: "dropdown"
        }, /*#__PURE__*/React.createElement("button", {
            type: "button",
            className: "btn dropdown-color dropdown-toggle",
            "data-bs-toggle": "dropdown"
        }, "Categories"), /*#__PURE__*/React.createElement("ul", {
            className: "dropdown-menu"
        }, this.state.categories.map(category => /*#__PURE__*/React.createElement("li", {
            key: category
        }, /*#__PURE__*/React.createElement("a", {
            className: "dropdown-item",
            "data-category": category,
            onClick: this.props.onClick != null && this.props.onClick
        }, category))))));
    }

}

class SubtopicsDropDown extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            subtopics: [],
            selectedSubtopics: new Set(),
            pageCategory: this.props.pageCategory
        };
    }

    componentDidMount() {
        this.getSubTopics();
    }

    getSubTopics() {
        fetch(`/api/category/subtopics?category=${this.state.pageCategory}`, {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': Cookies.get('XSRF-TOKEN'),
                'Content-Type': 'application/json; charset=utf-8'
            }
        }).then(response => response.json()).then(data => {
            this.state.selectedSubtopics = new Set();
            this.setState({
                subtopics: data
            });
            if (this.props.onLoad != null) this.props.onLoad(this.state.subtopics);
        });
    }

    render() {
        if (this.props.pageCategory !== this.state.pageCategory) {
            this.state.pageCategory = this.props.pageCategory;
            this.getSubTopics();
            return null;
        }

        const onSubtopicCheck = event => {
            const subtopic = event.target.dataset.subtopic;

            if (!event.target.checked) {
                this.state.selectedSubtopics.delete(subtopic);
            } else {
                this.state.selectedSubtopics.add(subtopic);
            }

            if (this.props.onSubtopicsUpdate != null) this.props.onSubtopicsUpdate(this.state.selectedSubtopics);
        };

        const className = "collapse" + (this.props.checked != null ? " show" : "");
        return /*#__PURE__*/React.createElement("div", {
            className: "collapse-area container mt-3 text-center"
        }, /*#__PURE__*/React.createElement("a", {
            href: "#subtopics-collapse",
            className: "btn dropdown-color",
            "data-bs-toggle": "collapse"
        }, "Subtopics"), /*#__PURE__*/React.createElement("div", {
            id: "subtopics-collapse",
            className: className
        }, this.state.subtopics.map(subtopic => {
            const checked = this.props.checked != null && this.props.checked.includes(subtopic);
            if (checked) this.state.selectedSubtopics.add(subtopic);
            return /*#__PURE__*/React.createElement(React.Fragment, {
                key: subtopic
            }, /*#__PURE__*/React.createElement("label", {
                className: "checkbox-dropdown-label"
            }, subtopic), /*#__PURE__*/React.createElement("input", {
                defaultChecked: checked,
                "data-subtopic": subtopic,
                onClick: onSubtopicCheck,
                type: "checkbox"
            }));
        })));
    }

}