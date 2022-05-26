# Load .env if it exists
if [ -f .env ]; then
  export $(echo $(cat .env | sed 's/#.*//g'| xargs) | envsubst)
fi

# Update javadoc
cd ./geotiff-tools || exit
./gradlew javadoc
rsync -avu --delete ./build/docs/javadoc/ ../documentation/public/javadoc

# Build documentation
cd ../documentation || exit
npm run build

# Upload to S3 bucket and invalidate cloudfront distribution
cd ./dist || exit
aws s3 sync . s3://"$S3_BUCKET_NAME"
aws cloudfront create-invalidation --distribution-id "$CLOUDFRONT_DISTRIBUTION_ID" --paths "/*"

