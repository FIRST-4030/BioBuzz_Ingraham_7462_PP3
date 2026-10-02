package org.firstinspires.ftc.teamcode.UtilOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.Command;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;



@Autonomous
    public class ExampleAuto extends OpMode {
        private Follower follower;
        private final PoseFactory poseFactory = PoseFactory.degrees();

        // Poses
        private final Pose q = poseFactory.of(0, 0, 0);
        private final Pose w = poseFactory.of(12, 0, 0);
        private final Pose e = poseFactory.of(12, 12, 0);
        private final Pose r = poseFactory.of(0, 12, 0);

        // Path methods
        private Path one() {
            return line(q, w).constant(0);
        }

        //linear(q, w)

        private Path two(){
            return line(w, e).constant(0);
        }

        private Path three(){
            return line(e, r).constant(0);
        }

        private Path four(){
            return line(r, q).constant(0);
        }

        private Command autoRoutine() {
            return sequential(
                    follow(follower, one()),
                    // Add mechanism commands here.
                    follow(follower, two()),
                    follow(follower, three()),
                    follow(follower, four())

            );
        }

        @Override
        public void init() {
            Scheduler.reset();

            follower = Constants.create(hardwareMap);
            follower.setPose(q);
            follower.update();
        }

        @Override
        public void start() {
            schedule(autoRoutine());
        }

        @Override
        public void loop() {
            follower.update();
            Scheduler.execute();
            // add your other methods needed in the loop here

//            telemetryData.addData("X", follower.pose().x());
//            telemetryData.addData("Y", follower.pose().y());
//            telemetryData.addData("Heading", Math.toDegrees(follower.pose().heading()));
            telemetry.addData("Follower Mode", follower.mode());
            telemetry.update();
        }
    }

