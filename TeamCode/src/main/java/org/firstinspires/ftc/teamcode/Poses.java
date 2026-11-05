package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;

public class Poses {
    private final double FORWARD = 90;
    private final double ROBOT_SIZE = 17;
    //constructor and other important stuff
    private final PoseFactory poseFactory = PoseFactory.degrees();
    public Poses(){}

    //left side park
    public final Pose startPosLeft = poseFactory.of(63, 5, FORWARD);
    public final Pose shootPosLeft = poseFactory.of(56, 20, FORWARD);

    public final Pose collectControlPosLeft = poseFactory.of(35, 10 , FORWARD);
    public final Pose collectPosLeft = poseFactory.of(13, 10, FORWARD + 90);

    public final Pose parkControlPosLeft = poseFactory.of(20, 49 , FORWARD);
    public final Pose parkPosLeft = poseFactory.of(9, 94, FORWARD - 90);

    //worst case
    public final Pose parkNoHeading = poseFactory.of(9, 94, FORWARD);
    public final Pose zero = poseFactory.of(0,0, FORWARD);

    //right side park
    public final Pose startPosRight = poseFactory.of(78, ROBOT_SIZE, FORWARD);
    public final Pose shootPosRight = poseFactory.of(82, 30, FORWARD);
    public final Pose preParkPosRight = poseFactory.of(144 - ROBOT_SIZE, ROBOT_SIZE, FORWARD);
    public final Pose parkPosRight = poseFactory.of(144 - ROBOT_SIZE, 30, FORWARD);




    //Useful methods

    public Pose of (double x, double y, double heading) {
        return poseFactory.of(x, y, heading);
    }

    public Pose of (double x, double y) {
        return poseFactory.of(x, y, FORWARD);
    }

}
