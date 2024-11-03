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
        })
            .then(response => response.json())
            .then(data => this.setState({categories: data}));
    }

    render() {
        if (this.props.newCategories === true && this.state.newCategories === false) {
            this.state.newCategories = true;
            this.getCategories();
            return null;
        }
        return (
            <div className={"container mt-3 text-center"}>
                <div className="dropdown">
                    <button type="button" className="btn dropdown-color dropdown-toggle" data-bs-toggle="dropdown">
                        Categories
                    </button>
                    <ul className="dropdown-menu">
                        {this.state.categories.map(category =>
                            <li key={category}><a className="dropdown-item" data-category={category}
                                                  onClick={this.props.onClick != null && this.props.onClick}>{category}</a>
                            </li>
                        )}
                    </ul>
                </div>
            </div>
        );
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
        })
            .then(response => response.json())
            .then(data => {
                this.state.selectedSubtopics = new Set();
                this.setState({subtopics: data});
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
        }
        const className = "collapse" + (this.props.checked != null ? " show" : "");
        return (
            <div className="collapse-area container mt-3 text-center">
                <a href="#subtopics-collapse" className="btn dropdown-color" data-bs-toggle="collapse">Subtopics</a>
                <div id="subtopics-collapse" className={className}>
                    {this.state.subtopics.map(subtopic => {
                            const checked = this.props.checked != null && this.props.checked.includes(subtopic);
                            if (checked) this.state.selectedSubtopics.add(subtopic)
                            return (<React.Fragment key={subtopic}>
                                <label
                                    className="checkbox-dropdown-label">{subtopic}</label><input defaultChecked={checked} data-subtopic={subtopic}
                                                                                                 onClick={onSubtopicCheck}
                                                                                                 type="checkbox"/>
                            </React.Fragment>)
                        }
                    )}
                </div>
            </div>
        );
    }
}