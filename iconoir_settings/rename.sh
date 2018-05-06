git filter-branch --env-filter '
WRONG_EMAIL="wrong@example.com"
NEW_NAME="Joseph"
NEW_EMAIL="jym@berkeley.edu"
export GIT_COMMITTER_NAME="$NEW_NAME"
export GIT_COMMITTER_EMAIL="$NEW_EMAIL"
export GIT_AUTHOR_NAME="$NEW_NAME"
export GIT_AUTHOR_EMAIL="$NEW_EMAIL"
' --tag-name-filter cat -- --branches --tags