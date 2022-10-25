from docs_conf.conf import *

branch = 'latest'
master_doc = 'index'
release = "{release}"
version = "{release}"

linkcheck_ignore = [
    'http://localhost',
]

intersphinx_mapping = {}

html_last_updated_fmt = '%d-%b-%y %H:%M'

def setup(app):
    app.add_css_file("css/ribbon.css")
