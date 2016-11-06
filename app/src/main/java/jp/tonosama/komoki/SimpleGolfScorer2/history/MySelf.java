package jp.tonosama.komoki.SimpleGolfScorer2.history;

/**
 * @author S130692
 */
class MySelf {

    /**  */
    private float mBestPad = 5.0F;
    /**  */
    private int mBestLastHalf = 100;
    /**  */
    private int mBestFirstHalf = 100;
    /**  */
    private int mBestHalf = 100;
    /**  */
    private int mBestTotal = 200;
    /**  */
    private int mBestHole = 10;
    /**  */
    private int mBestPottential = 0;
    /**  */
    private float mBestParOn = 0.0F;
    /**  */
    private int mCount = 0;
    /**  */
    private float mTmpAvg = 0;
    /**  */
    private float mTotalScoreAvg = 0;
    /**  */
    private float mTotalPadAvg = 0;

    /**
     * @return パット数
     */
    public float getBestPad() {
        return mBestPad;
    }

    /**
     * @param bestPad パット数
     */
    public void setBestPad(final float bestPad) {
        mBestPad = bestPad;
    }

    /**
     * @return ベストハーフ
     */
    public int getBestHalf() {
        return mBestHalf;
    }

    /**
     * @param bestHalf ベストハーフ
     */
    public void setBestHalf(final int bestHalf) {
        mBestHalf = bestHalf;
    }

    /**
     * @return 後半スコア
     */
    public int getBestLastHalf() {
        return mBestLastHalf;
    }

    /**
     * @param bestLastHalf 後半スコア
     */
    public void setBestLastHalf(final int bestLastHalf) {
        mBestLastHalf = bestLastHalf;
    }

    /**
     * @return 前半スコア
     */
    public int getBestFirstHalf() {
        return mBestFirstHalf;
    }

    /**
     * @param bestFirstHalf 前半スコア
     */
    public void setBestFirstHalf(final int bestFirstHalf) {
        mBestFirstHalf = bestFirstHalf;
    }

    /**
     * @return トータル
     */
    public int getBestTotal() {
        return mBestTotal;
    }

    /**
     * @param bestTotal トータル
     */
    public void setBestTotal(final int bestTotal) {
        mBestTotal = bestTotal;
    }

    /**
     * @return ベストホール
     */
    public int getBestHole() {
        return mBestHole;
    }

    /**
     * @param bestHole ベストホール
     */
    public void setBestHole(final int bestHole) {
        mBestHole = bestHole;
    }

    /**
     * @return ポテンシャル
     */
    public int getBestPottential() {
        return mBestPottential;
    }

    /**
     * @param bestPottential ポテンシャル
     */
    public void setBestPottential(final int bestPottential) {
        mBestPottential = bestPottential;
    }

    /**
     * @return パーオン率
     */
    public float getBestParOn() {
        return mBestParOn;
    }

    /**
     * @param bestParOn パーオン率
     */
    public void setBestParOn(final float bestParOn) {
        mBestParOn = bestParOn;
    }

    /**
     * @return 平均
     */
    public float getTmpAvg() {
        return mTmpAvg;
    }

    /**
     * @param tmpAvg 平均
     */
    public void setTmpAvg(final float tmpAvg) {
        this.mTmpAvg = tmpAvg;
    }

    /**
     * @return スコア平均
     */
    public float getTotalScoreAvg() {
        return mTotalScoreAvg;
    }

    /**
     * @param totalScoreAvg スコア平均
     */
    public void setTotalScoreAvg(final float totalScoreAvg) {
        mTotalScoreAvg = totalScoreAvg;
    }

    /**
     * @return パット数平均
     */
    public float getTotalPadAvg() {
        return mTotalPadAvg;
    }

    /**
     * @param totalPadAvg パット数平均
     */
    public void setTotalPadAvg(final float totalPadAvg) {
        mTotalPadAvg = totalPadAvg;
    }

    /**
     * @return カウント
     */
    public int getCount() {
        return mCount;
    }

    /**
     * @param count カウント
     */
    public void setCount(final int count) {
        mCount = count;
    }
};