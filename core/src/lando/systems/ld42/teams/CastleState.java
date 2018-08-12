package lando.systems.ld42.teams;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;

public class CastleState {

    private enum FlagState { none, raisePlayer, lowerPlayer, wavePlayer, raiseEnemy, lowerEnemy, waveEnemy}

    private final Assets assets;
    private FlagState _flagState;
    private Team.Type _team;
    private Team.Type _currentTeam;

    private TextureRegion _image;
    private float _animState = 0;
    private float _waveState = 0;

    public CastleState(Team.Type teamType) {
        assets = LudumDare42.game.assets;
        _team = _currentTeam = teamType;
        _flagState = (teamType == Team.Type.enemy) ? FlagState.raiseEnemy : FlagState.raisePlayer;
        _image = assets.castleRaiseAnimationPlayer.getKeyFrame(0);
    }

    public void update(float dt) {
        switch (_flagState) {
            case none:
                _image = assets.castleRaiseAnimationPlayer.getKeyFrame(0);
                break;
            case raisePlayer:
                _animState += dt;
                if (assets.castleRaiseAnimationPlayer.isAnimationFinished(_animState)) {
                    _flagState = FlagState.wavePlayer;
                }
                _image = assets.castleRaiseAnimationPlayer.getKeyFrame(_animState);
                break;
            case lowerPlayer:
                _animState -= dt;
                if (_animState < 0) {
                    _animState = 0;
                    _flagState = FlagState.raiseEnemy;
                }
                _image = assets.castleRaiseAnimationPlayer.getKeyFrame(_animState);
                break;
            case raiseEnemy:
                _animState += dt;
                if (assets.castleRaiseAnimationEnemy.isAnimationFinished(_animState)) {
                    _flagState = FlagState.waveEnemy;
                }
                _image = assets.castleRaiseAnimationEnemy.getKeyFrame(_animState);
                break;
            case lowerEnemy:
                _animState -= dt;
                if (_animState < 0) {
                    _animState = 0;
                    _flagState = FlagState.raisePlayer;
                }
                _image = assets.castleRaiseAnimationEnemy.getKeyFrame(_animState);
                break;
            case waveEnemy:
                _waveState += dt;
                _image = assets.castleWaveAnimationEnemy.getKeyFrame(_waveState);
                break;
            case wavePlayer:
                _waveState += dt;
                _image = assets.castleWaveAnimationPlayer.getKeyFrame(_waveState);
                break;
        }
    }

    public void raiseFlag(Team.Type team) {
        if (_currentTeam == team) return;
        switch (_currentTeam) {
            case player:
                _flagState = FlagState.lowerPlayer;
                break;
            case enemy:
                _flagState = FlagState.lowerEnemy;
                break;
        }
        _currentTeam = team;
    }

    public void switchTeam() {
        raiseFlag(_currentTeam == Team.Type.player ? Team.Type.enemy : Team.Type.player);
    }

    public TextureRegion getImage() {
        return _image;
    }
}
