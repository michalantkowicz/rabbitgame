package com.mantkowdev.rabbitgame;

import com.mantkowdev.rabbitgame.actors.Player;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static com.mantkowdev.rabbitgame.SteeringDirection.DOWN;
import static com.mantkowdev.rabbitgame.SteeringDirection.LEFT;
import static com.mantkowdev.rabbitgame.SteeringDirection.RIGHT;
import static com.mantkowdev.rabbitgame.SteeringDirection.UP;
import static java.lang.Math.round;

@RequiredArgsConstructor
public class SteeringPlugin implements Plugin<Player> {
    private final GameEventService gameEventService;

    private final Velocity velocity = new Velocity(2f);

    @Override
    public void handle(Player object) {
        Optional<SteeringEvent> event = gameEventService.getEvent(SteeringEvent.class, "steering_player");

        if (event.isPresent()) {
            doHandle(event.get());
        } else {
            velocity.stop();
        }

        if (!willCollide(object, object.getX() + velocity.getX(), object.getY() + velocity.getY())) {
            object.setX(object.getX() + velocity.getX());
            object.setY(object.getY() + velocity.getY());
        } else {
            if (velocity.getX() != 0) {
                handleVerticalAlignment(object, object.getX() + velocity.getX());
            } else if (velocity.getY() != 0) {
                handleHorizontalAlignment(object, object.getY() + velocity.getY());
            }
        }
    }

    public void doHandle(GameEvent event) {
        if (event.getEventObject() == UP) {
            velocity.setUp();
        } else if (event.getEventObject() == DOWN) {
            velocity.setDown();
        } else if (event.getEventObject() == LEFT) {
            velocity.setLeft();
        } else if (event.getEventObject() == RIGHT) {
            velocity.setRight();
        }
    }

    private boolean handleVerticalAlignment(Player player, float x) {
        int y = round(player.getY());

        for (int i = 1; i <= 15; i++) {
            if (!willCollide(player, x, y)) {
                player.setY(y); // position rounding issue
                return true;
            } else if (!willCollide(player, x, y + i)) {
                if (i > 1) {
                    player.setY(y + velocity.getMaxVelocity());
                } else {
                    player.setY(y + 1);
                }
                return true;
            } else if (!willCollide(player, x, y - i)) {
                if (i > 1) {
                    player.setY(y - velocity.getMaxVelocity());
                } else {
                    player.setY(y - 1);
                }
                return true;
            }
        }

        return false;
    }

    private boolean handleHorizontalAlignment(Player player, float y) {
        int x = round(player.getX());

        for (int i = 1; i <= 15; i++) {
            if (!willCollide(player, x, y)) {
                player.setX(x); // position rounding issue
                return true;
            } else if (!willCollide(player, x + i, y)) {
                if (i > 1) {
                    player.setX(x + velocity.getMaxVelocity());
                } else {
                    player.setX(x + 1);
                }
                return true;
            } else if (!willCollide(player, x - i, y)) {
                if (i > 1) {
                    player.setX(x - velocity.getMaxVelocity());
                } else {
                    player.setX(x - 1);
                }
                return true;
            }
        }

        return false;
    }

    private boolean willCollide(Player player, float x, float y) {
        return Arrays.stream(player.getStage().getActors().items)
                .anyMatch(actor ->
                        actor != null &&
                                !actor.equals(player) &&
                                doOverlap(
                                        x, y + player.getHeight(),
                                        x + player.getWidth(), y,
                                        actor.getX(), actor.getY() + actor.getHeight(),
                                        actor.getX() + actor.getWidth(), actor.getY()
                                )
                );
    }

    private boolean doOverlap(float tlx1, float tly1, float brx1, float bry1, float tlx2, float tly2, float brx2, float bry2) {
        return !(tlx1 >= brx2) && !(tlx2 >= brx1) && !(tly1 <= bry2) && !(tly2 <= bry1);
    }
}
